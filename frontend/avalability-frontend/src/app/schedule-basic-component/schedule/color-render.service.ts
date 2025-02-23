import { Injectable } from '@angular/core';
import {EventRenderedArgs} from "@syncfusion/ej2-angular-schedule";

@Injectable({
  providedIn: 'root'
})
export class ColorRender {

  //renderizado de colores de eventos
  public onEventRendered(args: EventRenderedArgs): void {
    const subject = args.data["Subject"];
    const startTime = args.data["StartTime"];
    const endTime = args.data["EndTime"];

    // Formato para mostrar las horas en un formato legible
    const formattedStartTime = new Date(startTime).toLocaleTimeString([], {hour: '2-digit', minute: '2-digit'});
    const formattedEndTime = new Date(endTime).toLocaleTimeString([], {hour: '2-digit', minute: '2-digit'});


    // Cambiar el color según el subject (al modificar la ausencia para que muestre la hora, me descentra los otros itema)
    switch (subject) {

      case 'HOLIDAY':
        args.element.style.backgroundColor = '#ff0202'; // Color para Meeting
        //subjectElement.innerHTML = `${subject}`;
        //args.element.innerHTML = defaultHTML;
        /*<div style="display: flex; flex-direction: column; align-items: center; justify-content: center; width: 100%; height: 100%;">
                                    <div style="width: 100%; text-align: center;">${subject}</div>
                                  </div>`*/
        break;

      case 'ABSENCE':
        args.element.style.backgroundColor = '#575757'; // Color para Teletrabajo
        //añadir la hora
        //console.log(args.element.innerHTML)
        args.element.innerHTML = `<div style="display: flex; flex-direction: column; align-items: center; justify-content: center; width: 100%; height: 100%;">
                                    <div style="width: 100%; text-align: center;">${subject}</div>
                                    <div style="font-size: 11px; width: 100%; text-align: center;">
                                      ${formattedStartTime} - ${formattedEndTime}
                                    </div>
                                  </div>`;
        break;

      case 'SICKLEAVE':
        args.element.style.backgroundColor = '#6029ef'; // Color para Training
        //args.element.innerHTML = defaultHTML
        //console.log('DEFAULTHTML:', args.element.innerHTML)
        /*`<div style="display: flex; flex-direction: column; align-items: center; justify-content: center; width: 100%; height: 100%;">
                                    <div style="width: 100%; text-align: center;">${subject}</div>
                                  </div>`*/
        break;

      case 'BANKDAY':
        args.element.style.backgroundColor = '#f3ac27'; // Color para Training
        //args.element.innerHTML = defaultHTML
        /*`<div style="display: flex; flex-direction: column; align-items: center; justify-content: center; width: 100%; height: 100%;">
                                    <div style="width: 100%; text-align: center;">${subject}</div>
                                  </div>`*/
        break;

      case 'TELEWORK':
        args.element.style.backgroundColor = '#ff8138'; // Color para Training
        //args.element.innerHTML = defaultHTML
        break;

      default:
        args.element.style.backgroundColor = '#000000'; // Color por defecto
        break;
    }
  }

}
