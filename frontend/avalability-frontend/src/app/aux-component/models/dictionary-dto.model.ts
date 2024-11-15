/**
 * DTO de diccionario
 */
export class DictionaryDTO {
    anumber: string;
    name: string;
    team: string;
    location: string;
    holiday: string;

    constructor(
      object: {
        anumber?: string;
        name?: string;
        team?: string;
        location?: string;
        holiday?: string;
      } = {}
    ) {
      this.anumber = object.anumber;
      this.name = object.name;
      this.team = object.team;
      this.location = object.location;
      this.holiday = object.holiday;
    }
  }
