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
  public toasts = [
    { title: 'Success !', content: 'Item has been sent successfully.', cssClass: 'e-toast-success'},
    { title: 'Success !', content: 'Item has been delete successfully.', cssClass: 'e-toast-success'},
    { title: 'Error !', content: 'Conflict with existing Item.', cssClass: 'e-toast-danger' },
    { title: 'Error !', content: 'Entity not found.', cssClass: 'e-toast-danger' },
    { title: 'Error !', content: 'Bad Request. (check the data)', cssClass: 'e-toast-danger' },
    { title: 'Error !', content: 'Internal Server Error', cssClass: 'e-toast-danger' },
    { title: 'Warning !', content: 'One day maximum (NOT SENT)', cssClass: 'e-toast-warning' } //6
  ];

  constructor(private toastService: ToastService) {
    this.toastService.messages$.subscribe(message => {
      this.showToast(message)
    })
  }

  showToast(type: number) {
    this.toastComponent.show(this.toasts[type]);
  }
}
