import {Component, ViewChild} from '@angular/core';
import {ToastComponent, ToastModule} from "@syncfusion/ej2-angular-notifications";
import {ToastService} from "./toast.service";

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [
    ToastModule
  ],
  templateUrl: './toast.component.html',
  styleUrl: './toast.component.scss'
})
export class ToastScheduleComponent {

  @ViewChild('toastComponent') public toastComponent: ToastComponent;

  public position = { X: 'Right', Y: 'Bottom' };

  constructor(private toastService: ToastService) {
    this.toastService.messages$.subscribe(message => {
      this.showToast(message);
    });
  }

  showToast(message: { title: string, content: string, cssClass: string }) {
    this.toastComponent.show(message);
  }
}
