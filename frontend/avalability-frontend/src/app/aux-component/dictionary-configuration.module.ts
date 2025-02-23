import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
//import { SharedModule } from 'src/app/shared/shared.module';
//import { DynamicFormsModule } from '@dekra/dynamic-forms-lib';
//import { CustomComponentsModule } from '@dekra/custom-components-lib';
import { DictionaryConfigurationRoutingModule } from './dictionary-configuration-routing.module';
import { DictionaryConfigurationComponent } from './components/dictionary-configuration.component';
import { DictionaryConfigurationDeleteDialogComponent } from './dialog/delete-dialog/dictionary-configuration-delete-dialog.component';
import {MatIcon} from "@angular/material/icon";
import {MatFormField} from "@angular/material/form-field";
import {MatOption, MatSelect} from "@angular/material/select";
import {
  MatCell,
  MatFooterCell,
  MatFooterRow,
  MatHeaderCell,
  MatHeaderRow,
  MatRow,
  MatTable
} from "@angular/material/table";
import {MatProgressBar} from "@angular/material/progress-bar";
import {ReactiveFormsModule} from "@angular/forms";
import {MatCheckbox} from "@angular/material/checkbox";
import {MatPaginator} from "@angular/material/paginator";
import {RouterModule, Routes} from "@angular/router";
import { MatTableModule } from '@angular/material/table';
import {HTTP_INTERCEPTORS} from '@angular/common/http';
import {DictionaryConfigurationService} from "./services/dictionary-configuration.service";
import { MatInputModule } from '@angular/material/input';
import {MatButtonModule} from "@angular/material/button";
import {ToolbarComponent} from "../schedule-basic-component/toolbar/toolbar.component";
import {authInterceptor} from "../interceptors/auth.interceptor";

const routes: Routes = [
  { path: '', component: DictionaryConfigurationComponent } // Ruta principal para el módulo
];

@NgModule({
  declarations: [DictionaryConfigurationComponent, DictionaryConfigurationDeleteDialogComponent],
  imports: [
    CommonModule,
    DictionaryConfigurationRoutingModule,
    MatIcon,
    MatFormField,
    MatSelect,
    MatOption,
    MatTable,
    MatProgressBar,
    MatHeaderCell,
    MatCell,
    MatFooterCell,
    ReactiveFormsModule,
    MatCheckbox,
    MatPaginator,
    MatFooterRow,
    MatRow,
    MatHeaderRow,
    MatTableModule,
    MatInputModule,
    MatButtonModule,
    RouterModule.forChild(routes),
    ToolbarComponent
  ],
    exports: [RouterModule, DictionaryConfigurationComponent],
  providers: [DictionaryConfigurationService,  { provide: HTTP_INTERCEPTORS, useFactory: () => authInterceptor, multi: true }]
})
export class DictionaryConfigurationModule {}
