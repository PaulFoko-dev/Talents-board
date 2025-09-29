import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Featuresgrid } from './featuresgrid';

describe('Featuresgrid', () => {
  let component: Featuresgrid;
  let fixture: ComponentFixture<Featuresgrid>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Featuresgrid]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Featuresgrid);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
