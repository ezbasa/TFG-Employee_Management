/*import {RouterModule, Routes} from '@angular/router';
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

export class AppRoutingModule { }*/


import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { LoginComponent } from './login/login.component'; // Asegúrate de importar el componente de login
import { ScheduleEmployeeComponent } from './schedule-basic-component/schedule/schedule.component';
import { DictionaryConfigurationModule } from './aux-component/dictionary-configuration.module';
import {TeamworkComponent} from "./teamwork/teamwork.component";

export const routes: Routes = [

  { path: 'login', loadComponent: () => LoginComponent },

  { path: 'calendar', loadComponent: ()=> ScheduleEmployeeComponent},

  { path: 'employees', loadChildren: () => import('./aux-component/dictionary-configuration.module').then(m => m.DictionaryConfigurationModule)},

  { path: 'teamwork', loadComponent: () => TeamworkComponent },

  // Redirección a la ruta inicial si no se encuentra una ruta
  { path: '**', redirectTo: 'login', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes), DictionaryConfigurationModule, ScheduleEmployeeComponent],
  exports: [RouterModule]
})
export class AppRoutingModule {}
