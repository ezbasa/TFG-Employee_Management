import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import {Router, RouterModule} from '@angular/router';
import {HttpClient} from "@angular/common/http";
import {MatCard} from "@angular/material/card";
import { MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {MatButtonModule} from "@angular/material/button";
import {MatIconModule} from "@angular/material/icon";
import {ToastService} from "../schedule-basic-component/toast/toast.service";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule, MatCard, MatFormFieldModule, MatInputModule, MatButtonModule, MatIconModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  loginForm: FormGroup;
  private apiUrl = 'http://localhost:8080/auth/login'; // URL del backend

  constructor(
    private fb: FormBuilder,
    private http: HttpClient, // Cliente HTTP para las peticiones
    private router: Router, // Router para redirección tras login
    private toastService: ToastService
  ) {
    // Crear el formulario
    this.loginForm = this.fb.group({
      employeeAnumber: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  // Método que se ejecuta al enviar el formulario
  onSubmit() {
    if (this.loginForm.valid) {
      localStorage.removeItem('token'); // Elimina token viejo
      // Enviar las credenciales al backend
      this.http.post(this.apiUrl, this.loginForm.value).subscribe({
        next: (response: any) => {
          console.log('Respuesta del backend:', response);

          // Guardar el token JWT en localStorage
          if (response?.token) {
            localStorage.setItem('token', response.token);
            localStorage.setItem('role', response.role);
            // Redirigir al usuario (ejemplo: /dashboard)
            console.log("role: ", localStorage.getItem('role'));
            this.router.navigate(['/calendar']);
          } else {
            console.error('Token no recibido');
          }
        },
        error: (err) => {
          this.toastService.showToast(err.error, 'error');
          console.error('Error en la autenticación:', err);
        }
      });
    } else {
      console.log('Formulario inválido');
    }
  }

}
