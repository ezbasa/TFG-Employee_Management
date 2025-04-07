import { Injectable } from '@angular/core';
import {Subject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  //private messagesSubject = new Subject<number>();
  private messagesSubject = new Subject<{ title: string, content: string, cssClass: string }>();
  messages$ = this.messagesSubject.asObservable();

  constructor() { }

  showToast(content: string, type: 'success' | 'error' | 'warning') {
    let title: string;
    let cssClass: string;

    switch (type) {
      case 'success':
        title = 'Success !';
        cssClass = 'e-toast-success';
        break;
      case 'error':
        title = 'Error !';
        cssClass = 'e-toast-danger';
        break;
      case 'warning':
        title = 'Warning !';
        cssClass = 'e-toast-warning';
        break;
      default:
        title = 'Information !';
        cssClass = 'e-toast-info';
    }

    this.messagesSubject.next({title, content, cssClass});
  }
}
