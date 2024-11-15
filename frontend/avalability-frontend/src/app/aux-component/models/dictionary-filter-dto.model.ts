/**
 * DTO de filtro de diccionario
 */
export class DictionaryFilterDTO {
  anumber: string;
  name: string;
  team: string;
  location: string;

  constructor(
    object: {
      anumber?: string;
      name?: string;
      location?: string;
      team?: string;
    } = {}
  ) {
    this.anumber = object.anumber;
    this.name = object.name;
    this.location = object.location;
    this.team = object.team;
  }
}
