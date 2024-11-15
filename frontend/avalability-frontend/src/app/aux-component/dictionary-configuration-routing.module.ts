import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DictionaryConfigurationComponent } from './components/dictionary-configuration.component';

const routes: Routes = [
  {
    path: '',
    component: DictionaryConfigurationComponent
    // canActivate: [AuthGuard],
    // canLoad: [AuthGuard]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DictionaryConfigurationRoutingModule {}
