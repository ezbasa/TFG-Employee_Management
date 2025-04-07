import {Component, ViewEncapsulation, ViewChild, OnInit} from '@angular/core';
import {
  EventSettingsModel,
  View,
  GroupModel,
  TimelineViewsService,
  TimelineMonthService,
  TimelineYearService,
  DayService,
  ResizeService,
  DragAndDropService,
  ScheduleComponent,
  ScheduleAllModule,
  WorkHoursModel,
  ActionEventArgs,
  ExportOptions,
  ExportFieldInfo,
  PopupOpenEventArgs,
  PopupCloseEventArgs, EJ2Instance,
} from '@syncfusion/ej2-angular-schedule';
import { ItemModel } from '@syncfusion/ej2-navigations';
import {
  MultiSelectModule,
  DropDownList,
  ChangeEventArgs,
  CheckBoxSelectionService,
  DropDownListModule
} from '@syncfusion/ej2-angular-dropdowns';
import {DialogAllModule} from '@syncfusion/ej2-angular-popups';
import {HttpClient, HttpErrorResponse, HttpParams} from "@angular/common/http";
import {MatToolbarModule} from "@angular/material/toolbar";
import { MatButtonModule } from '@angular/material/button';
import { extend, isNullOrUndefined } from '@syncfusion/ej2-base';
import { FormsModule, ReactiveFormsModule } from '@angular/forms'
import {ClickEventArgs} from "@syncfusion/ej2-angular-buttons";
import {DropDownButton} from '@syncfusion/ej2-angular-splitbuttons'
import { CommonModule} from "@angular/common";
import {DateTimePickerModule} from "@syncfusion/ej2-angular-calendars";
import { FormValidator } from "@syncfusion/ej2-angular-inputs";
import {ToastService} from "../toast/toast.service";
import {ColorRender} from "./color-render.service";
import {ToastScheduleComponent} from "../toast/toast.component";

@Component({
  selector: 'app-schedule',
  templateUrl: 'schedule.component.html',
  styleUrls: ['schedule.component.scss'],
  encapsulation: ViewEncapsulation.None,
  providers: [DayService, TimelineViewsService, TimelineMonthService, TimelineYearService, ResizeService, DragAndDropService, CheckBoxSelectionService],
  standalone: true,
  imports: [ScheduleAllModule, MatToolbarModule, /*MatIcon,*/ MatButtonModule, DialogAllModule, FormsModule, ReactiveFormsModule, MultiSelectModule, /*JsonPipe,*/ DateTimePickerModule, DropDownListModule, CommonModule, /*ToolbarComponent,*/ ToastScheduleComponent]
})

export class ScheduleEmployeeComponent implements OnInit {
  @ViewChild('scheduleObj') public scheduleObj: ScheduleComponent;

  public items: any = null;
  public data: Record<string, any>[] = [];

  public selectedDate: Date = new Date(Date.now());
  public currentView: View = 'TimelineMonth';
  public employeeDataSource: Record<string, any>[] = [];
  public group: GroupModel = { enableCompactView: false, resources: ['Employee', 'holidays'] };
  public showQuickInfo: Boolean = false;
  public allowMultiple = false;
  public eventSettings: EventSettingsModel = {
    fields: {
      id: 'Id',
      subject: { name: 'Subject' },
      location: {name: 'Location'},
      startTime: { name: 'StartTime' },
      endTime: { name: 'EndTime' },
      description: { name: 'descripton'}
    }
  };
  public scheduleHours: WorkHoursModel  = { highlight: true, start: '07:00', end: '17:00' };
  public originalEmployeeData: any = null; //variable para guardar a TODOS los empleados

  constructor(private http: HttpClient, private toastService: ToastService, public colorService: ColorRender) {}

  //primera carga de datos (obtengo la fecha manualmente)
  ngOnInit(): void {
    const date = new Date();
    const firstDay = new Date(date.getFullYear(), date.getMonth(), 1);
    const lastDay = new Date(date.getFullYear(), date.getMonth() + 1, 0);

    //console.log("CALENDARIO LANZADO")
    this.sendRequest(firstDay.toISOString(), lastDay.toISOString());
  }

  //Declaracion para la renderización de los filtros
  public onActionBegin(args: ActionEventArgs) {
    if (args.requestType === "toolbarItemRendering") {
      // Filtro para los Items
      let subjectFilter: ItemModel = { align: 'Right', template: ' <input type="text" id="subject"/>', type: 'Input' };
      // Filtro para equipos
      let teamFilter: ItemModel = { align: 'Right', template: '<input type="text" id="teamFilter"/>', type: 'Input' };

      const dropdownButtonItem: ItemModel = {
        align: 'Right',
        template: '<button id="dropdownButton"></button>', // Aquí solo declaramos el botón
      };

      args.items.push(subjectFilter, teamFilter);

      const userRole = localStorage.getItem('role');
      if (userRole === 'PROJECT_MANAGER') { // Cambia según el rol que necesites
        args.items.push(dropdownButtonItem);
      }
    }
  }

  //DROPDOWN----------------------------------
  public dropdownItems = [
    { text: 'Bank day', iconCss: 'e-icons e-plus' },
    { text: 'Export to Excel', iconCss: 'e-icons e-export-excel' }
  ];

  // Función para inicializar el DropDownButton
  private initializeDropDownButton(): void {
   const dropDownButton = new DropDownButton({
      items: this.dropdownItems,
      select: (args: ClickEventArgs) => this.onDropDownItemClick(args),
      iconCss: 'e-icons e-menu'
    }, '#dropdownButton');
  }

  // Gestionar la selección de opciones del DropDownButton
  private onDropDownItemClick(args: ClickEventArgs): void {
    //console.log("ITEMCLICK", args.element.ariaLabel)
    if (args.element.ariaLabel === 'Bank day') {
      this.onCreateEventClick();
    } else if (args.element.ariaLabel === 'Export to Excel') {
      this.onExportClick();
    }
  }

  //CREACIÓN BANKDAY
  public showLocation: boolean = false;
  public bankday: string[] = ['BANKDAY']

  public onCreateEventClick() {
    this.showLocation = true;

    const item  = {
      id : null,
      Subject : 'BANKDAY',
      description : "",
      startDate : null,
      endDate : null,
      EmployeeId : null,
      location: null,
    }

    this.scheduleObj.openEditor(item,'Add');
  }

  //EXPORTACIÓN A EXCEL
  public onExportClick(): void {
    const exportFields: ExportFieldInfo[] = [
      { name: 'EmployeeId', text: 'Anumber' },
      { name: 'Subject', text: 'Summary' },
      { name: 'StartTime', text: 'Start Date' },
      { name: 'EndTime', text: 'End Date' }
    ];
    const exportValues: ExportOptions = { fieldsInfo: exportFields };
    this.scheduleObj.exportToExcel(exportValues);
  }
  //-------------------------------------------------------------------------------------

  //CONTROL DE PETICIONES Y TRANSICIONES ENTRE VISTAS --------------------------------------------------------

   //Enviar peticion http
  calendarURL = "http://localhost:8080/employee/range" //Recoge todos los empleados, item entre unas fechas
  itemURL = "http://localhost:8080/item-calendar" //Acciones crud de los items

  //gestion de evnetos
  public captionActionComplete(event){

    switch (event.requestType){

      case "toolBarItemRendered":
          this.filter()
        this.initializeDropDownButton();
        break;

      case "eventCreated":

            if(event.data[0].Subject == 'BANKDAY'){
              const dia = event.data[0].EndTime - event.data[0].StartTime
              const miliSecondInOneDay = 1000 * 60 * 60 * 24;
              //console.log("dias", dia)
              if(dia > miliSecondInOneDay){
                //mandar toast
                this.toastService.showToast('Must be older than 24 h', 'error');
                this.newRangeDate()
              }else{
                this.addItem(event)
              }

            }else{
              this.addItem(event);
            }
        break;

      case "eventChanged":

        if(event.data[0].Subject == 'BANKDAY'){
          const dia = event.data[0].EndTime - event.data[0].StartTime
          const miliSecondInOneDay = 1000 * 60 * 60 * 24;
          //console.log("dias", dia)
          if(dia > miliSecondInOneDay){
            //mandar toast
            this.toastService.showToast('Must be older than 24 h', 'error');
            this.newRangeDate()
          }else{
            this.updateItem(event)
          }

        }else{
          this.updateItem(event);
        }
        break;

      case "dateNavigate":
          this.newRangeDate();
        break;

      case "eventRemoved":
          this.deleteItem(event)
        break;

      case "viewNavigate":
          this.newRangeDate()
        break;
    }
    //console.log(event)
  }

  private addItem(data) {
    //console.log("ADDITEM",data)
    const i = this.createItem(data); //creo el item
    i.id = null

    //peticion para crear item
    this.http.post(this.itemURL, i)
      .subscribe(
        response =>{
          this.toastService.showToast('Added event', 'success');
          this.newRangeDate();
        },
        error => {
          this.toastService.showToast(error.error, 'error');
          this.newRangeDate();
        }
      )
  }

  private updateItem(data){
    this.Anumber = data.data[0].EmployeeId //imagino que uso esto aquí, por si modifico sin abrir pop-up
    console.log(this.Anumber)
    const i = this.createItem(data); //creo el item

    //console.log(data)

    //peticion para actualizar item
    this.http.put(this.itemURL, i)
      .subscribe(
        response =>{
          this.toastService.showToast('Updated event', 'success');
          this.newRangeDate();
        },
        error => {
          this.toastService.showToast(error.error, 'error');
          this.newRangeDate()
        }
      )
  }

  private deleteItem(data){

    const id = data.data[0].Id;
    const params = new HttpParams()
      .set('id', id as number)
      .set('anumber', this.Anumber as string);


    this.http.delete(this.itemURL, {params}) .subscribe({
        next: (response) =>{
          this.toastService.showToast('eliminated event', 'success');
          this.newRangeDate();
        },
        error: (error: HttpErrorResponse) =>{
          this.toastService.showToast(error.error, 'error');
          this.newRangeDate()
        }}
    );
  }

  private createItem(data){
    const datos = data.data[0];

    //modificación de fecha final
    const endDate = new Date(datos.EndTime);
    endDate.setSeconds(endDate.getSeconds() - 1);

    const item = {
      id : datos.Id,
      itemType : datos.Subject,
      description : datos.Description,
      startDate : datos.StartTime.toISOString(),
      endDate : endDate.toISOString(),
      employeeAnumber : this.Anumber as string,
      location: datos.Location
    }
    this.Anumber = null;//para que no guarde el valor

    return item;
  }

  private newRangeDate(){
    const currentViewDates: Date[] = this.scheduleObj.getCurrentViewDates() as Date[];
    const startDate: Date = new Date(currentViewDates[0]);
    const endDate: Date = new Date(currentViewDates[currentViewDates.length - 1]);

    //sumo un día (al convertirlo a ISO me devuelve el día anterior al restarle las 2 horas)
    // arreglo para recoger todo el mes (mostrar item el último día del calendario)
    endDate.setDate(endDate.getDate() + 1);

    this.sendRequest(startDate.toISOString(), endDate.toISOString())
  }

  //recepcion de datos del backend y transformacion para Scheduler
  sendRequest(startDate: String | null, endDate: String | null) {
    //parametros
    const params = new HttpParams()
      .set('startDate', startDate as string)
      .set('endDate', endDate as string)

    console.log("fechas" + startDate + " fin " + endDate)

    this.http.get<any[]>(this.calendarURL, {params} )
      .subscribe(
        response => {
          this.employeeDataSource = this.employeeMapping(response); //mapeo empleados
          this.items = this.itemsMapping(response);//mapeo items
          this.data = extend([], this.items, null, true) as Record<string, any>[];

          // Actualizar la configuración de eventos
          this.eventSettings = {
            dataSource: this.data
          }
          this.keepFilter()
        },
        error => {
          this.toastService.showToast(error.error, "error")
        }
      );
  }

  private employeeMapping(response: any){
    this.originalEmployeeData = response.map(employee => ({
      anumber: employee.anumber,
      name: employee.name,
      team: employee.team,
      location: employee.location,
      holidays: employee.holiday
    }));

    return this.originalEmployeeData;
  }

  private itemsMapping(response: any) {
    const arr = [];

    // Iterar sobre el array de empleados
    response.forEach(emp => {
      // Verificar si calendarItemDTOS existe y tiene elementos
      if (emp.calendarItemDTOS && emp.calendarItemDTOS.length > 0) {
        emp.calendarItemDTOS.forEach(item => {
          arr.push({
            Id: item.id,
            Subject: item.itemType,
            Location: item.location,
            StartTime: new Date(item.startDate),
            EndTime: new Date(item.endDate),
            EmployeeId: item.employeeAnumber,
            Description: item.description
          });
        });
      }
    });
    return arr;
  }

//FILTROS DROPDOWN----------------------------------------------------------
  //variables para complementacion de los filtros
  private dataItemFilter: Record<string, any>[]
  private dataTeamFilter: Record<string, any>[]
  private filterItem: boolean = false;
  private filterTeam: boolean = false;
  private subjectDropdown: DropDownList;
  private teamDropdown: DropDownList;

  filter() {
    // Dropdown para el filtro de "Subject"
    this.subjectDropdown = new DropDownList({
      dataSource: [
        { text: "All", value: "" },
        { text: "Absence", value: "ABSENCE" },
        { text: "Sickleave", value: "SICKLEAVE" },
        { text: "Bankday", value: "BANKDAY" },
        { text: "Telework", value: "TELEWORK" },
        { text: "Holiday", value: "HOLIDAY" }
      ],
      fields: { text: "text", value: 'value' },
      placeholder: 'Select Type',
      change: (args: ChangeEventArgs) => {
        const selectedSubject = args.value;

        if (selectedSubject === "") {
          this.filterItem = false;  // Desactiva el filtro de Subject
          this.dataItemFilter = [];  // Limpia el filtro

        } else {
          this.filterItem = true;  // Activa el filtro de Subject

          // Filtrar los datos por el Subject seleccionado y guardarlos en dataItemFilter
          this.dataItemFilter = this.data.filter((appointment) => {
            return appointment["Subject"] === selectedSubject;
          });
        }

        this.applyCombinedFilters(); // Aplica los filtros combinados
      }
    });
    this.subjectDropdown.appendTo('#subject');

    // Dropdown para el filtro de "Team"
    this.teamDropdown = new DropDownList({
      dataSource: [
        { text: "All", value: "" },
        { text: "Back", value: "Back" },
        { text: "Front", value: "Front" },
        { text: "QA", value: "QA" },
        { text: "Support", value: "Support" }
      ],
      fields: { text: 'text', value: 'value' },
      placeholder: 'Select Expert',
      change: (args: ChangeEventArgs) => {
        const selectedTeam = args.value;

        if (!selectedTeam) {
          this.filterTeam = false;  // Desactiva el filtro de Team
          this.dataTeamFilter = [];  // Limpia el filtro

        } else {
          this.filterTeam = true;  // Activa el filtro de Team

          // Filtrar los datos por el Team seleccionado y guardarlos en dataTeamFilter
          this.dataTeamFilter = this.data.filter((appointment) => {
            const employeeTeam = this.originalEmployeeData.find(emp => emp['anumber'] === appointment["EmployeeId"])?.['team'];
            return employeeTeam === selectedTeam;
          });
        }

        this.applyCombinedFilters(); // Aplica los filtros combinados
      }
    });
    this.teamDropdown.appendTo('#teamFilter');
  }

  //mantener el filtro activo al cambiar de vista
  keepFilter() {
    setTimeout(() => {
      // Ejecutar el evento `change` para que se apliquen los filtros actuales
      if (this.subjectDropdown.value) {
        this.subjectDropdown.change({ value: this.subjectDropdown.value } as ChangeEventArgs);
      }
      if (this.teamDropdown.value) {
        this.teamDropdown.change({ value: this.teamDropdown.value } as ChangeEventArgs);
      }
    }, 100)
  }

// Función que aplica los filtros combinados
  applyCombinedFilters() {
    //debugger
    let combinedFilteredData: Record<string, any>[] = this.data;
    this.employeeDataSource = this.originalEmployeeData;

    // Aplica el filtro de Subject si está activo
    if (this.filterItem) {
      combinedFilteredData = combinedFilteredData.filter(appointment =>
        this.dataItemFilter.includes(appointment)
      );
    }

    // Aplica el filtro de Team si está activo
    if (this.filterTeam) {
      combinedFilteredData = combinedFilteredData.filter(appointment =>
        this.dataTeamFilter.includes(appointment)
      );
    }

    // Actualiza la fuente de datos del Scheduler con los datos filtrados
    this.scheduleObj.eventSettings.dataSource = combinedFilteredData.length > 0 ? combinedFilteredData : [];

    if(this.filterItem || this.filterTeam) {
      this.updateFilteredEmployees(combinedFilteredData);
    }
  }

// Nueva función para filtrar empleados que tienen elementos activos
  updateFilteredEmployees(filteredData: Record<string, any>[]) {
    // Obtener los EmployeeId únicos de los datos filtrados
    const employeeIdsWithActiveItems = new Set(filteredData.map(appointment => appointment["EmployeeId"]));

    // Filtrar el dataSourceEmployee para mostrar solo los empleados que tienen eventos activos
    this.employeeDataSource = this.originalEmployeeData.filter(emp =>
      employeeIdsWithActiveItems.has(emp['anumber'])
    );
  }

  //NEW POPUP-----------------------------------------------------------------

  public subjectOptions: string[] = ['ABSENCE', 'SICKLEAVE', 'TELEWORK', 'HOLIDAY'];
  public locationOption: string[] = ['MADRID','MALAGA'];
  public startDate!: Date;
  public endDate!: Date;
  public Anumber: String; //esta variable está creada por algún inconveniente del uso del anumber en Syncfusion

  //rellenar datos de empleado
  public selectedEmployee: any = null
  onPopupOpen(args: PopupOpenEventArgs): void {

    this.optionPopUp(args)

    if (args.type === 'Editor') {

      this.selectedEmployee = this.originalEmployeeData.find(emp => emp.anumber === args.data["EmployeeId"])

      if(args.data["IsAllDay"] == true) {
        const endDate = new Date(args.data["StartTime"]).setHours(23, 59, 59)
        args.data["EndTime"] = new Date(endDate);
      }

      //Validacion del campo Subject
      if (!isNullOrUndefined(document.getElementById("EventType_Error")as any)) {
        document.getElementById("EventType_Error")!.style.display = "none";
        document.getElementById("EventType_Error")!.style.left = "351px";
      }
      let formElement: HTMLElement = <HTMLElement>args.element.querySelector('.e-schedule-form');
      let validator: FormValidator = ((formElement as EJ2Instance).ej2_instances[0] as FormValidator);
      validator.addRules('Subject', { required: true });

      //guardo el ID para usarlo al enviar la peticion correspondiente
      this.Anumber = args.data['EmployeeId'];
    }
  }

  onPopupClose(args: PopupCloseEventArgs): void {
    //console.log("CERRADO")
    this.showLocation = false;
  }

  //cancelar la seleccion de fin de semana
  optionPopUp(event){
    //modificacion del popup
    if(event.data["Subject"] == 'BANKDAY'){
      this.showLocation = true;
    }

    //no permitir seleccionar fines de semana
    let diaSemana = event.data.StartTime.getDay();

    // Verificar si es sábado (6) o domingo (0)
    if (diaSemana === 6 || diaSemana === 0) {
      event.cancel = true;
    }
  }

}
