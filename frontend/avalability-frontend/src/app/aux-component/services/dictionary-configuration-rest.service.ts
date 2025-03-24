import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
//import { environment } from 'src/environments/environment';
import {DictionaryEntry} from "../models/dictionary-entry.model";

//AHORA MISMO HE CREADO UNA VARIARABLE ENVIRONMENT PARA QUITAR EL ERROR Y SEGUIR CON EL PROCESO

@Injectable({
  providedIn: 'root'
})
export class DictionaryConfigurationRestService {
  constructor(protected http: HttpClient) {}

  employeeURL: string = 'http://localhost:8080/employee'
  employeeGetURL: string = 'http://localhost:8080/employee/modification'

  // Obtenemos todos los diccionarios.
  getDictionaries(): Observable<any> {
    return this.http.get<any>(this.employeeGetURL);
  }

  // Funcion para eliminar una fila del diccionario
  deleteRow(anumber_row) {
    const params = new HttpParams()
      .set('anumber', anumber_row as string)

    return this.http.delete(this.employeeURL, {params});
  }

  // Funcion para crear una nueva entrada en el diccionario
  createDictionaryEntry(form) {
    return this.http.post<any>(this.employeeURL, form);
  }

  // Funcion para modificar una entrada ya existente en el diccionario
  modifyDictionaryEntry(form) {
    return this.http.put<any>(this.employeeURL, form);
  }
}
