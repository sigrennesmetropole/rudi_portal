import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {DialogData} from "../../home/home.component";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Router} from "@angular/router";
import {LogService} from "../../core/services/log.service";
import {AuthenticationService} from "../../core/services/authentication.service";


@Component({
  selector: 'app-login',
  templateUrl: './login-dialog.component.html',
  styleUrls: ['./login-dialog.component.scss']
})
export class LoginDialogComponent implements OnInit {
  loginForm: FormGroup;
  isSubmitted = false;
  hide = true;
  // Message échec de connexion
  showError = false;

  constructor(
    private readonly logService: LogService,
    private readonly authentificationService: AuthenticationService,
    private formBuilder: FormBuilder,
    public dialogRef: MatDialogRef<LoginDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData) {

    // Initialisation des controles du formulaire
    this.loginForm = this.formBuilder.group({
      login: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  ngOnInit(): void {

  }

  get formControls() {
    return this.loginForm.controls;
  }

  /**
   * Fonction permettant d'effacer le champ mot de passe en cas d'erreur
   */
  resetPassword() {
    this.formControls.password.setValue('');
  }

  /**
   * Fermetture de la popine de connexion
   */
  onNoClick() {
    this.dialogRef.close();
  }

  /**
   * Function permettant de soumettre de formule de connexion
   */
  submitForm() {
    this.isSubmitted = true;
    if (this.loginForm.invalid) {
      return;
    }
    this.authentificationService.authenticate(this.loginForm).subscribe(
      () => {
        // Appel de la methode pour la fermeture de la popine
        this.onNoClick();
      },
      () => {
        this.resetPassword();
        this.showError = true;
      });
  }

}
