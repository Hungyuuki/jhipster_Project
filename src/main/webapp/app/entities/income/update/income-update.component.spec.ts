import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { IncomeService } from '../service/income.service';
import { IIncome, Income } from '../income.model';

import { IncomeUpdateComponent } from './income-update.component';

describe('Income Management Update Component', () => {
  let comp: IncomeUpdateComponent;
  let fixture: ComponentFixture<IncomeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let incomeService: IncomeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [IncomeUpdateComponent],
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
      .overrideTemplate(IncomeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(IncomeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    incomeService = TestBed.inject(IncomeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const income: IIncome = { id: 456 };

      activatedRoute.data = of({ income });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(income));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Income>>();
      const income = { id: 123 };
      jest.spyOn(incomeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ income });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: income }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(incomeService.update).toHaveBeenCalledWith(income);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Income>>();
      const income = new Income();
      jest.spyOn(incomeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ income });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: income }));
      saveSubject.complete();

      // THEN
      expect(incomeService.create).toHaveBeenCalledWith(income);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Income>>();
      const income = { id: 123 };
      jest.spyOn(incomeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ income });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(incomeService.update).toHaveBeenCalledWith(income);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
