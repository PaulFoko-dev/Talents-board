import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProfilEntreprise } from './profil-entreprise';

describe('ProfilEntreprise', () => {
  let component: ProfilEntreprise;
  let fixture: ComponentFixture<ProfilEntreprise>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProfilEntreprise]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProfilEntreprise);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
