import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IMoney, Money } from '../money.model';

import { MoneyService } from './money.service';

describe('Money Service', () => {
  let service: MoneyService;
  let httpMock: HttpTestingController;
  let elemDefault: IMoney;
  let expectedResult: IMoney | IMoney[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(MoneyService);
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

    it('should create a Money', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Money()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Money', () => {
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

    it('should partial update a Money', () => {
      const patchObject = Object.assign(
        {
          name: 'BBBBBB',
          roll: 'BBBBBB',
          income: 1,
        },
        new Money()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Money', () => {
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

    it('should delete a Money', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addMoneyToCollectionIfMissing', () => {
      it('should add a Money to an empty array', () => {
        const money: IMoney = { id: 123 };
        expectedResult = service.addMoneyToCollectionIfMissing([], money);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(money);
      });

      it('should not add a Money to an array that contains it', () => {
        const money: IMoney = { id: 123 };
        const moneyCollection: IMoney[] = [
          {
            ...money,
          },
          { id: 456 },
        ];
        expectedResult = service.addMoneyToCollectionIfMissing(moneyCollection, money);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Money to an array that doesn't contain it", () => {
        const money: IMoney = { id: 123 };
        const moneyCollection: IMoney[] = [{ id: 456 }];
        expectedResult = service.addMoneyToCollectionIfMissing(moneyCollection, money);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(money);
      });

      it('should add only unique Money to an array', () => {
        const moneyArray: IMoney[] = [{ id: 123 }, { id: 456 }, { id: 54625 }];
        const moneyCollection: IMoney[] = [{ id: 123 }];
        expectedResult = service.addMoneyToCollectionIfMissing(moneyCollection, ...moneyArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const money: IMoney = { id: 123 };
        const money2: IMoney = { id: 456 };
        expectedResult = service.addMoneyToCollectionIfMissing([], money, money2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(money);
        expect(expectedResult).toContain(money2);
      });

      it('should accept null and undefined values', () => {
        const money: IMoney = { id: 123 };
        expectedResult = service.addMoneyToCollectionIfMissing([], null, money, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(money);
      });

      it('should return initial array if no Money is added', () => {
        const moneyCollection: IMoney[] = [{ id: 123 }];
        expectedResult = service.addMoneyToCollectionIfMissing(moneyCollection, undefined, null);
        expect(expectedResult).toEqual(moneyCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
