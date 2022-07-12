export interface IIncome {
  id?: number;
  name?: string | null;
  roll?: string | null;
  income?: number;
}

export class Income implements IIncome {
  constructor(public id?: number, public name?: string | null, public roll?: string | null, public income?: number) {}
}

export function getIncomeIdentifier(income: IIncome): number | undefined {
  return income.id;
}
