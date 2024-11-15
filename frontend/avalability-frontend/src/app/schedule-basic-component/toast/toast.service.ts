import { Injectable } from '@angular/core';
import {Subject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  private messagesSubject = new Subject<number>();
  messages$ = this.messagesSubject.asObservable();

  constructor() { }

  showToast(message: number) {
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
      case 500:
        code = 5
        break;
    }

    this.messagesSubject.next(code);
  }
}
