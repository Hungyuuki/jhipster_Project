import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMoney, getMoneyIdentifier } from '../money.model';

export type EntityResponseType = HttpResponse<IMoney>;
export type EntityArrayResponseType = HttpResponse<IMoney[]>;

@Injectable({ providedIn: 'root' })
export class MoneyService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/monies');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(money: IMoney): Observable<EntityResponseType> {
    return this.http.post<IMoney>(this.resourceUrl, money, { observe: 'response' });
  }

  update(money: IMoney): Observable<EntityResponseType> {
    return this.http.put<IMoney>(`${this.resourceUrl}/${getMoneyIdentifier(money) as number}`, money, { observe: 'response' });
  }

  partialUpdate(money: IMoney): Observable<EntityResponseType> {
    return this.http.patch<IMoney>(`${this.resourceUrl}/${getMoneyIdentifier(money) as number}`, money, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IMoney>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMoney[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addMoneyToCollectionIfMissing(moneyCollection: IMoney[], ...moniesToCheck: (IMoney | null | undefined)[]): IMoney[] {
    const monies: IMoney[] = moniesToCheck.filter(isPresent);
    if (monies.length > 0) {
      const moneyCollectionIdentifiers = moneyCollection.map(moneyItem => getMoneyIdentifier(moneyItem)!);
      const moniesToAdd = monies.filter(moneyItem => {
        const moneyIdentifier = getMoneyIdentifier(moneyItem);
        if (moneyIdentifier == null || moneyCollectionIdentifiers.includes(moneyIdentifier)) {
          return false;
        }
        moneyCollectionIdentifiers.push(moneyIdentifier);
        return true;
      });
      return [...moniesToAdd, ...moneyCollection];
    }
    return moneyCollection;
  }
}
