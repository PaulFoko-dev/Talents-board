import { TestBed } from '@angular/core/testing';

import { ProfilEntrepriseService } from './profilEntreprise.service';

describe('ProfilEntreprise', () => {
  let service: ProfilEntrepriseService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProfilEntrepriseService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
