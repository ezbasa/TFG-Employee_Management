import { Injectable } from '@angular/core';
import { DictionaryConfigurationRestService } from './dictionary-configuration-rest.service';
import { Observable } from 'rxjs';
import {DictionaryEntry} from "../models/dictionary-entry.model";
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class DictionaryConfigurationService {
  constructor(protected dictionaryConfigurationRestService: DictionaryConfigurationRestService) {}

  // Obtenemos un observable de array con todos los campos y
  // valores de los diccionarios.
  getDictionaries(): Observable<any> {
    return this.dictionaryConfigurationRestService
      .getDictionaries()
      .pipe(
        map(dictionaryArray =>
          dictionaryArray.map(dictionaryEntry => new DictionaryEntry(dictionaryEntry))
        )
      );
  }

  // Funcion para eliminar una fila del diccionario
  deleteRow(id_row) {
    return this.dictionaryConfigurationRestService.deleteRow(id_row);
  }

  // Funcion para crear una nueva entrada en el diccionario
  createDictionaryEntry(form) {
    return this.dictionaryConfigurationRestService.createDictionaryEntry(form);
  }

  // Funcion para modificar una entrada ya existente en el diccionario
  modifyDictionaryEntry(form) {
    return this.dictionaryConfigurationRestService.modifyDictionaryEntry(form);
  }
}
