import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {ScheduleEmployeeComponent} from "./schedule-basic-component/schedule/schedule.component";
import {ToastScheduleComponent} from "./schedule-basic-component/toast/toast.component";
import {ToolbarComponent} from "./schedule-basic-component/toolbar/toolbar.component";



@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    ScheduleEmployeeComponent,
    ToastScheduleComponent,
    ToolbarComponent
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'avalability-frontend';
}
