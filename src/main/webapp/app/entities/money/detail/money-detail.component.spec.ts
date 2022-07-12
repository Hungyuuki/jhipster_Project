import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { MoneyDetailComponent } from './money-detail.component';

describe('Money Management Detail Component', () => {
  let comp: MoneyDetailComponent;
  let fixture: ComponentFixture<MoneyDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MoneyDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ money: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(MoneyDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(MoneyDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load money on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.money).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
