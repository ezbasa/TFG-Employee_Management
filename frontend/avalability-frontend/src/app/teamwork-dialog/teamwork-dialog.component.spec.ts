import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TeamworkDialogComponent } from './teamwork-dialog.component';

describe('TeamworkDialogComponent', () => {
  let component: TeamworkDialogComponent;
  let fixture: ComponentFixture<TeamworkDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TeamworkDialogComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(TeamworkDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
