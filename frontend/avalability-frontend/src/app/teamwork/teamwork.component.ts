import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import {HttpClient, HttpErrorResponse, HttpParams} from "@angular/common/http";
import {MatDialog, MatDialogModule} from "@angular/material/dialog";
import { TeamworkDialogComponent } from '../teamwork-dialog/teamwork-dialog.component';
import {MatIcon} from "@angular/material/icon";
import {ToastService} from "../schedule-basic-component/toast/toast.service";

@Component({
  selector: 'app-teamwork',
  standalone: true,
  imports: [
    CommonModule,
    MatIcon,
    MatDialogModule
  ],
  templateUrl: './teamwork.component.html',
  styleUrls: ['./teamwork.component.scss'],
})
export class TeamworkComponent implements OnInit{

  members: Member[] = [];
  teams: Teamwork[] = [];

  private membersURL = 'http://localhost:8080/teamwork/members'; //recoger todos los miembros
  private teamworkURL = 'http://localhost:8080/teamwork'

  public showButton: boolean;

  constructor(private http: HttpClient, private dialog: MatDialog, private cdr: ChangeDetectorRef, private toastService: ToastService) {
  }

  ngOnInit() {
    this.checkrole();
    this.bringMembers();
    this.bringTeamwork();
  }

  //carga cada vez que se entra en el componente
  private bringMembers() {
    this.http.get<Member[]>(this.membersURL).subscribe({
      next: (response) => {
        this.members = response;
      },
      error: () => {
        console.log('Error al recoger empleados');
        this.toastService.showToast('Employees could not be loaded', 'error');
      }
    });
  }

  private bringTeamwork() {
    this.http.get<Teamwork[]>(this.teamworkURL)
      .subscribe({
        next: (response) => {
          this.teams = response;
        },
        error: () => {
          console.log('Error al recoger grupos')
          this.toastService.showToast('Teamworks could not be loaded', 'error');
        }
      });


  }

  private checkrole() {
    const role = localStorage.getItem("role");

    if(role == 'PROJECT_MANAGER') {
      this.showButton = true;
    }else{
      this.showButton = false;
    }
  }

  openDialog(team?: Teamwork): void {
    //console.log("abrimos dialogo")

    const dialogRef = this.dialog.open(TeamworkDialogComponent, {
      //podemos poner el ancho width
      data: {
        id: team?.id  || '',
        name: team?.name || '',
        description: team?.description || '',
        membersDTOS: team?.membersDTOS || [],
        allMembers: [...this.members]
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        if (team) {
          console.log('result: ', result)
          this.upadteTeamwork(result);
        } else {
          this.addTeamwork(result);
        }
      }
    });
  }

  deleteTeam(team: Teamwork) {
    this.deleteTeamwork(team.id);
    this.teams = this.teams.filter(t => t.id !== team.id); // Eliminar equipo de la lista local
  }

  //COMUNICACIÓN CON EL BACK
  private addTeamwork(teamData: Teamwork){
    console.log('añadir:', teamData)

    this.http.post(this.teamworkURL, teamData)
      .subscribe({
        next: (response: Teamwork) => {
          this.teams.push(response);
          this.toastService.showToast('Added teamwork', "success");
          console.log('equipo añadido', response)
        },
        error: (error: HttpErrorResponse) => {
          console.log('Error al añadir: ', error.error)
          this.toastService.showToast(error.error, "error");
        }
      })
  }

  private upadteTeamwork(teamData: Teamwork){
    this.http.put(this.teamworkURL, teamData)
      .subscribe({
        next:(response :Teamwork)=>{
          const index = this.teams.findIndex(t => t.id === response.id);
          if (index !== -1) {
            this.teams = [...this.teams.slice(0, index), response, ...this.teams.slice(index + 1)];
          }
          this.cdr.detectChanges();
          this.toastService.showToast('Update teamwork', "success");
        },
        error:(error: HttpErrorResponse) =>{
          console.log('Error al actualizar: ', error.error)
          this.toastService.showToast(error.error, "error");
        }
      })
  }

  private deleteTeamwork(id: number){
    const params = new HttpParams()
      .set('teamWorkId', id as number)

    this.http.delete(this.teamworkURL, {params})
      .subscribe({
        next:(response)=>{
          //console.log('borrado BIEN')
          this.toastService.showToast('Deleted teamwork', "success");
        },
        error:(error: HttpErrorResponse) =>{
          console.log('error al borrar')
          this.toastService.showToast(error.error, "error");
        }
      })
  }


}

interface Member {
  anumber: string;
  name: string;
  expert: string;
}

interface Teamwork {
  id: number;
  name: string;
  description: string;
  teamLeader: string;
  membersDTOS: Member[];
}



/*membersMapping(response: any){
  this.members = response.map(member => ({
      anumber: member.anumber,
      name: member.name,
      expert: member.expert
    }))
}*/


/*teams: Team[] = [
  {
    id: 1,
    name: 'Proyecto Alpha',
    description: 'Desarrollo de aplicación web',
    members: [
      { anumber: 'A1001', name: 'Juan Pérez', function: 'Frontend' },
      { anumber: 'A1002', name: 'María López', function: 'Backend' },
      { anumber: 'A1003', name: 'Carlos Sánchez', function: 'DevOps' }
    ]
  },
  {
    id: 2,
    name: 'Proyecto Beta',
    description: 'Mantenimiento de API',
    members: [
      { anumber: 'A1004', name: 'Ana Torres', function: 'Backend' },
      { anumber: 'A1005', name: 'David Ruiz', function: 'QA' }
    ]
  }
];*/
