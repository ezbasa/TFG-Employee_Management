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

  /*showToast(message: number) {
  let code = message;
    switch (message){
      case 409:
        code = 2
        break;
      case 404:
        code = 3
        break;
      case 400:
        code = 4
        break;
      case 403:
        code = 5
        break;
      case 500:
        code = 6
        break;
    }

    this.messagesSubject.next(code);
  }*/

  /*
   { title: 'Success !', content: 'Item has been delete successfully.', cssClass: 'e-toast-success'},
    { title: 'Error !', content: 'Conflict with existing Item.', cssClass: 'e-toast-danger'
   */
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
