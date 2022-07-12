import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IIncome, Income } from '../income.model';

import { IncomeService } from './income.service';

describe('Income Service', () => {
  let service: IncomeService;
  let httpMock: HttpTestingController;
  let elemDefault: IIncome;
  let expectedResult: IIncome | IIncome[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(IncomeService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
      roll: 'AAAAAAA',
      income: 0,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Income', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Income()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Income', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          roll: 'BBBBBB',
          income: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Income', () => {
      const patchObject = Object.assign(
        {
          name: 'BBBBBB',
          income: 1,
        },
        new Income()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Income', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          roll: 'BBBBBB',
          income: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Income', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addIncomeToCollectionIfMissing', () => {
      it('should add a Income to an empty array', () => {
        const income: IIncome = { id: 123 };
        expectedResult = service.addIncomeToCollectionIfMissing([], income);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(income);
      });

      it('should not add a Income to an array that contains it', () => {
        const income: IIncome = { id: 123 };
        const incomeCollection: IIncome[] = [
          {
            ...income,
          },
          { id: 456 },
        ];
        expectedResult = service.addIncomeToCollectionIfMissing(incomeCollection, income);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Income to an array that doesn't contain it", () => {
        const income: IIncome = { id: 123 };
        const incomeCollection: IIncome[] = [{ id: 456 }];
        expectedResult = service.addIncomeToCollectionIfMissing(incomeCollection, income);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(income);
      });

      it('should add only unique Income to an array', () => {
        const incomeArray: IIncome[] = [{ id: 123 }, { id: 456 }, { id: 37847 }];
        const incomeCollection: IIncome[] = [{ id: 123 }];
        expectedResult = service.addIncomeToCollectionIfMissing(incomeCollection, ...incomeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const income: IIncome = { id: 123 };
        const income2: IIncome = { id: 456 };
        expectedResult = service.addIncomeToCollectionIfMissing([], income, income2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(income);
        expect(expectedResult).toContain(income2);
      });

      it('should accept null and undefined values', () => {
        const income: IIncome = { id: 123 };
        expectedResult = service.addIncomeToCollectionIfMissing([], null, income, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(income);
      });

      it('should return initial array if no Income is added', () => {
        const incomeCollection: IIncome[] = [{ id: 123 }];
        expectedResult = service.addIncomeToCollectionIfMissing(incomeCollection, undefined, null);
        expect(expectedResult).toEqual(incomeCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
