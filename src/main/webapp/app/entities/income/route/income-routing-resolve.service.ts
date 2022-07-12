import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IIncome, Income } from '../income.model';
import { IncomeService } from '../service/income.service';

@Injectable({ providedIn: 'root' })
export class IncomeRoutingResolveService implements Resolve<IIncome> {
  constructor(protected service: IncomeService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IIncome> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((income: HttpResponse<Income>) => {
          if (income.body) {
            return of(income.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Income());
  }
}
