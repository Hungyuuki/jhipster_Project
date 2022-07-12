import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { MoneyService } from '../service/money.service';
import { IMoney, Money } from '../money.model';

import { MoneyUpdateComponent } from './money-update.component';

describe('Money Management Update Component', () => {
  let comp: MoneyUpdateComponent;
  let fixture: ComponentFixture<MoneyUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let moneyService: MoneyService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [MoneyUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(MoneyUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MoneyUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    moneyService = TestBed.inject(MoneyService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const money: IMoney = { id: 456 };

      activatedRoute.data = of({ money });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(money));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Money>>();
      const money = { id: 123 };
      jest.spyOn(moneyService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ money });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: money }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(moneyService.update).toHaveBeenCalledWith(money);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Money>>();
      const money = new Money();
      jest.spyOn(moneyService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ money });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: money }));
      saveSubject.complete();

      // THEN
      expect(moneyService.create).toHaveBeenCalledWith(money);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Money>>();
      const money = { id: 123 };
      jest.spyOn(moneyService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ money });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(moneyService.update).toHaveBeenCalledWith(money);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
