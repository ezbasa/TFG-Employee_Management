import { Component, Inject } from '@angular/core';
import {MatDialogRef, MAT_DIALOG_DATA, MatDialogModule} from '@angular/material/dialog';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatSelectModule } from "@angular/material/select";
import { MatButtonModule } from "@angular/material/button";
import { MatInputModule } from '@angular/material/input';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-teamwork-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatFormFieldModule,
    MatSelectModule,
    MatButtonModule,
    MatInputModule,
    ReactiveFormsModule,
    MatDialogModule
  ],
  templateUrl: './teamwork-dialog.component.html',
  styleUrls: ['./teamwork-dialog.component.scss'],
})
export class TeamworkDialogComponent {
  groupForm: FormGroup;
  members: any[] = []; // Se almacena aquí la lista de miembros recibida del padre

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<TeamworkDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    // Creamos el formulario con los datos existentes (si los hay)
    this.groupForm = this.fb.group({
      id: [data?.id],
      name: [data?.name || '', Validators.required],
      description: [data?.description || ''],
      members: [data?.membersDTOS?.map((m: any) => m.anumber) || []]
    });

    // Recibir la lista de miembros desde los datos pasados al diálogo
    if (data?.allMembers) {
      //console.log("dentro dle if ahora", this.data.allMembers)
      this.members = data.allMembers;
    }
  }

  save() {
    if (this.groupForm.valid) {
      const formData = {
        ...this.groupForm.value,
        membersDTOS: this.getSelectedMembers()
      };
      this.dialogRef.close(formData);
    }
  }

  close() {
    this.dialogRef.close();
  }

  private getSelectedMembers(): any[] {
    return this.groupForm.value.members.map((anumber: string) => {
      return this.members.find(m => m.anumber === anumber)!;
    });
  }
}
