import { Component, OnInit, ViewChild, ChangeDetectorRef } from '@angular/core';
import { DictionaryConfigurationService } from '../services/dictionary-configuration.service';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { FormGroup, FormControl, FormBuilder } from '@angular/forms';
import { DictionaryFilterDTO } from '../models/dictionary-filter-dto.model';
import { debounceTime, distinctUntilChanged, distinct, startWith, map } from 'rxjs/operators';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { DictionaryConfigurationDeleteDialogComponent } from '../dialog/delete-dialog/dictionary-configuration-delete-dialog.component';
import { Observable, forkJoin } from 'rxjs';
import { DictionaryDTO } from '../models/dictionary-dto.model';
import {DictionaryEntry} from "../models/dictionary-entry.model";

@Component({
  selector: 'app-dictionary-configuration',
  templateUrl: './dictionary-configuration.component.html',
  styleUrls: ['./dictionary-configuration.component.scss'],
})
export class DictionaryConfigurationComponent implements OnInit {
  displayedColumns = ['name', 'anumber', 'location', 'team', 'role', 'holiday', 'iconFooter'];
  dictionaryData: any;
  dictionaryAllData: any;
  dictionaryDataBase: any;
  showCreate: Boolean;
  showEdit: Boolean;
  editingRowIndex: number;
  filterFormGroup: FormGroup;
  creationFormGroup: FormGroup;
  modificationFormGroup: FormGroup;
  filter: DictionaryFilterDTO;
  locationDropdown: any;
  loadingData: Boolean;

  public teamOptions: string[] = ['Back', 'Front', 'QA', 'Support'];
  public locationOption: string[] = ['MADRID','MALAGA'];
  public roleOption: string[] = ['PROJECT_MANAGER', 'EMPLOYEE'];

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  constructor(
    protected dictionaryConfigurationService: DictionaryConfigurationService,
    //protected alert: AlertService,
    private fb: FormBuilder,
    public dialog: MatDialog,
    public cd: ChangeDetectorRef
  ) {
    this.showCreate = false;
    this.showEdit = false;
    this.editingRowIndex = null;
    this.locationDropdown = [];

    this.filterFormGroup = this.fb.group({
      anumber: [''],
      name: [''],
      team: [''],
      //role: [''],
      location: [''],
      holiday: ['']
    });

    this.creationFormGroup = this.fb.group({
      anumber: [''],
      name: [''],
      team: [''],
      role: [''],
      location: [''],
      holiday: ['']
    });
  }

  /**
   *
   */
  ngOnInit() {
    this.getDictionaryData();

    this.filterFormGroup.valueChanges
      .pipe(debounceTime(0), distinctUntilChanged())
      .subscribe(payload => {
        this.applyFilter(payload);
      });
  }

  // Funcion para activar o no modificacion de una fila
  /**
   *
   * @param rowIndexToEdit
   * @param row
   */
  rowEditActivate(rowIndexToEdit: number, row?): void {
    if (row && this.editingRowIndex !== rowIndexToEdit) {
      this.modificationFormGroup = this.fb.group({
        anumber: [row.anumber],
        name: [row.name],
        team: [row.team],
        role:[row.role],
        location: [row.location],
        holiday: [row.holiday]
      });
    }
    this.editingRowIndex = rowIndexToEdit;
  }

  // Funcion para eliminar una fila del diccionario
  deleteRow(anumber_row) {
    this.dictionaryConfigurationService.deleteRow(anumber_row).subscribe(
      result => {
        //console.log('empleado borrado')
        /*this.getDictionaryData();
        this.alert.success(
          new AlertMessage({
            text: 'Diccionario eliminado correctamente'
          })
        );*/
      },
      error => {
        //console.error(error);
        /*this.alert.error(
          new AlertMessage({
            text: error.error.name
          })
        );*/
      }
    );
  }

  verifyData(formValues) {
    let isValid =
      formValues?.anumber !== null &&
      formValues?.anumber !== '' &&
      formValues?.name !== null &&
      formValues?.name !== '' &&
      formValues?.team !== null &&
      formValues?.team !== '' &&
      formValues?.role !== null &&
      formValues?.role !== '' &&
      formValues?.location !== null &&
      formValues?.location !== '' &&
      formValues?.holiday !== null &&
      formValues?.holiday !== ''

    return isValid;
  }

  // Funcion para crear una nueva entrada en el diccionario
  createDictionaryEntry(creationForm) {

    let dictionaryDTO: DictionaryDTO = new DictionaryDTO(creationForm);

    const isValid = this.verifyData(dictionaryDTO);
    if (isValid) {
      this.dictionaryConfigurationService.createDictionaryEntry(dictionaryDTO).subscribe(
        result => {
          this.getDictionaryData();
          /*this.alert.success(
            new AlertMessage({
              text: 'Diccionario creado correctamente'
            })
          );*/
        },
        error => {
          console.error(error);
          /*this.alert.error(
            new AlertMessage({
              text: error.error.name
            })
          );*/
        }
      );
    } else {
      console.log('NO ES VALIDO')
      /*this.alert.error(
        new AlertMessage({
          text: 'Los campos requeridos no son válidos'
        })
      );*/

    }
  }

  // Funcion para modificar una entrada existente en el formulario
  modifyDictionaryEntry(modifyForm) {

    let dictionaryDTO: DictionaryDTO = new DictionaryDTO(modifyForm);

    this.dictionaryConfigurationService.modifyDictionaryEntry(modifyForm).subscribe(
      result => {
        this.rowEditActivate(null);
        this.getDictionaryData();
        /*this.alert.success(
          new AlertMessage({
            text: 'Diccionario modificado correctamente'
          })
        );*/
      },
      error => {
        console.error(error);
        /*this.alert.error(
          new AlertMessage({
            text: error.error.name
          })
        );*/
      }
    );
  }

  // Funcion para obtener los datos que se van a mostrar en la tabla
  // con o sin filtros
  getDictionaryData(filter?: DictionaryFilterDTO) {
    this.loadingData = true;

    const dictionary$ = this.dictionaryConfigurationService.getDictionaries();

    forkJoin([dictionary$]).subscribe(([dictionaryValue]) => {

      this.dictionaryDataBase = dictionaryValue;

      this.filterFormGroup.updateValueAndValidity({ onlySelf: false, emitEvent: true });
      this.loadingData = false;
    })
  }

  // Funcion para reiniciar todos los valorres del filtro
  resetFilter() {
    this.filterFormGroup.setValue({
      anumber: '',
      name: '',
      team: '',
      location: '',
      holiday: ''
    });
  }

  resetCreationForm() {
    this.creationFormGroup.setValue({
      anumber: null,
      name: null,
      team: null,
      role: null,
      location: null,
      holiday: null
    });
  }

  // Funcion para mostrar ventana de confirmacion de borrado
  confirmDelete(row): void {
    //console.log('dentro de confirmdelete', row.name)
    const dialogRef = this.dialog.open(DictionaryConfigurationDeleteDialogComponent, {
      width: '400px',
      data: {
        anumber: row.anumber,
        name: row.name,
        /*team: row.team,
        location: row.location,//añadido
        holiday: row.holiday//añadido*/
      }
    });


    dialogRef.afterClosed().subscribe(result => {
      //console.log('RESUTL', result)
      if (result) {
        this.deleteRow(row.anumber);
      }
    });
  }

  // Funcion para aplicar el filtro
  applyFilter(payload) {
    this.filter = new DictionaryFilterDTO(payload);

    const filteredData = this.dictionaryDataBase.filter((data: DictionaryEntry) => {
      const isTrue =
        (data?.name?.toLowerCase()?.includes(this.filter.name.trim().toLowerCase()) ?? !this.filter.name) &&
        (data?.location?.includes(this.filter.location) ?? !this.filter.location) &&
        (data?.team?.includes(this.filter.team) ?? !this.filter.team) &&
        (data?.anumber?.toLowerCase()?.includes(this.filter.anumber.trim().toLowerCase()) ?? !this.filter.anumber)

      return isTrue;
    });

    this.dictionaryData = new MatTableDataSource(filteredData);
    this.dictionaryData.paginator = this.paginator;

    if(this.modificationFormGroup) {
      this.rowEditActivate(null);
      this.modificationFormGroup.reset();
    }
    if(this.creationFormGroup) {
      this.showCreate = false;
      this.resetCreationForm();
    }
  }

}
