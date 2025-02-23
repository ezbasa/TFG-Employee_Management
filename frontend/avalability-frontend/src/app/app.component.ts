import { Component } from '@angular/core';
import {NavigationEnd, RouterOutlet, Router} from '@angular/router';
//import {ScheduleEmployeeComponent} from "./schedule-basic-component/schedule/schedule.component";
//import {ToastScheduleComponent} from "./schedule-basic-component/toast/toast.component";
import {ToolbarComponent} from "./schedule-basic-component/toolbar/toolbar.component";
import { NgIf } from '@angular/common';


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    /*ScheduleEmployeeComponent,
    ToastScheduleComponent,*/
    ToolbarComponent,
    NgIf
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'avalability-frontend';

  isAuthenticated: boolean = false;

  constructor(private router: Router) {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.isAuthenticated = !!localStorage.getItem('token');
      }
    });
  }
}
