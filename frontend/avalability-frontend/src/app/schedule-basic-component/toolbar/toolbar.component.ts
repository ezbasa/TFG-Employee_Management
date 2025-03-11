import {Component, OnInit} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatToolbar} from "@angular/material/toolbar";
import {Router} from "@angular/router";
import {CommonModule} from "@angular/common";
import {HttpClient} from "@angular/common/http";
import {ToastService} from "../toast/toast.service";

@Component({
  selector: 'app-toolbar',
  standalone: true,
  imports: [
    MatButton,
    MatIcon,
    MatToolbar,
    CommonModule,

  ],
  templateUrl: './toolbar.component.html',
  styleUrl: './toolbar.component.scss'
})
export class ToolbarComponent implements OnInit {
  constructor(private router: Router, private http: HttpClient, private toastService: ToastService) {}

  public showButton;

  ngOnInit() {
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
    this.router.navigate(['employees']);
  }
  navigateToCalendar() {
    this.router.navigate(['calendar']);
  }

  logoutURL = "/auth/logout";
  sendlogout(){

    this.http.post(this.logoutURL, {}, { responseType: 'text' })
      .subscribe(
        response =>{
          localStorage.removeItem('token');
          this.router.navigate(['login']);
        },
        error => {
          console.log("ERROR" , error);
          this.toastService.showToast(error.status) //corregir
        }
      )
  }

  navigateToTeamwork() {
    this.router.navigate(['teamwork'])
  }
}
