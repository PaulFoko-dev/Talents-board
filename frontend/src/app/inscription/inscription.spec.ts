import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Inscription } from './inscription';

describe('inscription', () => {
  let component: Inscription;
  let fixture: ComponentFixture<Inscription>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Inscription]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Inscription);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
