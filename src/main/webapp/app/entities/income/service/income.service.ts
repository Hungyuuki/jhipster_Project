import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IIncome, getIncomeIdentifier } from '../income.model';

export type EntityResponseType = HttpResponse<IIncome>;
export type EntityArrayResponseType = HttpResponse<IIncome[]>;

@Injectable({ providedIn: 'root' })
export class IncomeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/incomes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(income: IIncome): Observable<EntityResponseType> {
    return this.http.post<IIncome>(this.resourceUrl, income, { observe: 'response' });
  }

  update(income: IIncome): Observable<EntityResponseType> {
    return this.http.put<IIncome>(`${this.resourceUrl}/${getIncomeIdentifier(income) as number}`, income, { observe: 'response' });
  }

  partialUpdate(income: IIncome): Observable<EntityResponseType> {
    return this.http.patch<IIncome>(`${this.resourceUrl}/${getIncomeIdentifier(income) as number}`, income, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IIncome>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IIncome[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addIncomeToCollectionIfMissing(incomeCollection: IIncome[], ...incomesToCheck: (IIncome | null | undefined)[]): IIncome[] {
    const incomes: IIncome[] = incomesToCheck.filter(isPresent);
    if (incomes.length > 0) {
      const incomeCollectionIdentifiers = incomeCollection.map(incomeItem => getIncomeIdentifier(incomeItem)!);
      const incomesToAdd = incomes.filter(incomeItem => {
        const incomeIdentifier = getIncomeIdentifier(incomeItem);
        if (incomeIdentifier == null || incomeCollectionIdentifiers.includes(incomeIdentifier)) {
          return false;
        }
        incomeCollectionIdentifiers.push(incomeIdentifier);
        return true;
      });
      return [...incomesToAdd, ...incomeCollection];
    }
    return incomeCollection;
  }
}
