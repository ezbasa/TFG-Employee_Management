import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatToolbar} from "@angular/material/toolbar";
import {Router} from "@angular/router";
import {CommonModule} from "@angular/common";
import {DictionaryConfigurationModule} from "../../aux-component/dictionary-configuration.module";
import {ScheduleEmployeeComponent} from "../schedule/schedule.component";
import {HttpClient, HttpParams} from "@angular/common/http";
import {ToastScheduleComponent} from "../toast/toast.component";
import {ToastService} from "../toast/toast.service";

@Component({
  selector: 'app-toolbar',
  standalone: true,
  imports: [
    MatButton,
    MatIcon,
    MatToolbar,
    CommonModule,
    //ToastScheduleComponent
   /* DictionaryConfigurationModule,
    ScheduleEmployeeComponent*/
  ],
  templateUrl: './toolbar.component.html',
  styleUrl: './toolbar.component.scss'
})
export class ToolbarComponent implements OnInit {
  constructor(private router: Router, private cd: ChangeDetectorRef, private http: HttpClient, private toastService: ToastService) {}

  public showButtonEmployee = true;
  public showButton;

  ngOnInit() {
   // console.log('Iniciado')
    this.checkrole();
  }

  private checkrole() {
    const role = localStorage.getItem("role");

    if(role == 'PROJECT_MANAGER') {
      this.showButton = true;
    }else{
      this.showButton = false;
    }
  }
  navigateToEmployees() {
    this.showButtonEmployee = !this.showButtonEmployee;
    this.router.navigate(['employees']);
  }
  navigateToCalendar() {
    this.showButtonEmployee = !this.showButtonEmployee;
    this.router.navigate(['calendar']);
  }

  logoutURL = "/auth/logout";
  sendlogout(){

    this.http.post(this.logoutURL, {}, { responseType: 'text' })
      .subscribe(
        response =>{
          console.log("logout BIEN RECIBIDO")
          this.toastService.showToast(0) //corregir acciones
          localStorage.removeItem('token');
          this.router.navigate(['login']);
        },
        error => {
          console.log("ERROR" , error);
          this.toastService.showToast(error.status) //corregir
        }
      )
  }
}
