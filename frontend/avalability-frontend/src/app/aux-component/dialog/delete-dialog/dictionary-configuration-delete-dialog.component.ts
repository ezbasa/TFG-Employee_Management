import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
//import { DialogData } from '@dekra/workflow-component-lib/lib/components/dialog/dialog.component';
//REVISAR------------------------------------------

@Component({
  selector: 'app-dictionary-configuration-delete-dialog',
  templateUrl: './dictionary-configuration-delete-dialog.component.html',
  styleUrls: ['./dictionary-configuration-delete-dialog.component.scss']
})
export class DictionaryConfigurationDeleteDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<DictionaryConfigurationDeleteDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {}

  closeDeleteDialog(result): void {
    if (result) {
      this.dialogRef.close(true);
    } else {
      this.dialogRef.close(false);
    }
  }
}
