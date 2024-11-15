/**
 * Transformación de DTO al los datos necesarios de entrada
 */
export class DictionaryEntry {
  anumber?: string;
  name?: string;
  team?: string;
  location?: string;
  holiday?: number;


  constructor(
    object: {
      anumber?: string;
      name?: string;
      team?: string;
      location?: string;
      holiday?: number;
    } = {}
  ) {
    this.anumber = object.anumber;
    this.name = object.name;
    this.team = object.team;
    this.location = object.location;
    this.holiday = object.holiday;
  }
}
