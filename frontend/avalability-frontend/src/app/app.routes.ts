import {RouterModule, Routes} from '@angular/router';
import {ScheduleEmployeeComponent} from "./schedule-basic-component/schedule/schedule.component";
import {DictionaryConfigurationModule} from "./aux-component/dictionary-configuration.module";
import {NgModule} from "@angular/core";

export const routes: Routes = [
  {path: '', loadComponent: () => ScheduleEmployeeComponent},
  {path: 'Employees',  loadChildren: () => import('../app/aux-component/dictionary-configuration.module').then(m => m.DictionaryConfigurationModule) }
];

@NgModule({
  imports: [RouterModule.forRoot(routes), DictionaryConfigurationModule],
  exports: [RouterModule]
})

export class AppRoutingModule { }
