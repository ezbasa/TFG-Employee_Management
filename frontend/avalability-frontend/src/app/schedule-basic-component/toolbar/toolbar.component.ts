import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatToolbar} from "@angular/material/toolbar";
import {Router} from "@angular/router";
import {CommonModule} from "@angular/common";
import {DictionaryConfigurationModule} from "../../aux-component/dictionary-configuration.module";
import {ScheduleEmployeeComponent} from "../schedule/schedule.component";

@Component({
  selector: 'app-toolbar',
  standalone: true,
  imports: [
    MatButton,
    MatIcon,
    MatToolbar,
    CommonModule,
   /* DictionaryConfigurationModule,
    ScheduleEmployeeComponent*/
  ],
  templateUrl: './toolbar.component.html',
  styleUrl: './toolbar.component.scss'
})
export class ToolbarComponent implements OnInit {
  constructor(private router: Router, private cd: ChangeDetectorRef) {}

  public showButtonEmployee = true;

  ngOnInit() {
   // console.log('Iniciado')
  }

  navigateToEmployees() {
    this.showButtonEmployee = !this.showButtonEmployee;
    this.router.navigate(['Employees']);
  }
  navigateToCalendar() {
    this.showButtonEmployee = !this.showButtonEmployee;
    this.router.navigate(['/']);
  }
}
