import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IMoney, Money } from '../money.model';
import { MoneyService } from '../service/money.service';

import { MoneyRoutingResolveService } from './money-routing-resolve.service';

describe('Money routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: MoneyRoutingResolveService;
  let service: MoneyService;
  let resultMoney: IMoney | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    routingResolveService = TestBed.inject(MoneyRoutingResolveService);
    service = TestBed.inject(MoneyService);
    resultMoney = undefined;
  });

  describe('resolve', () => {
    it('should return IMoney returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultMoney = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultMoney).toEqual({ id: 123 });
    });

    it('should return new IMoney if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultMoney = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultMoney).toEqual(new Money());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Money })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultMoney = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultMoney).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
