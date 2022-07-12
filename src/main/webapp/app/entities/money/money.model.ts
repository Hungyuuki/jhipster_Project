export interface IMoney {
  id?: number;
  name?: string | null;
  roll?: string;
  income?: number;
}

export class Money implements IMoney {
  constructor(public id?: number, public name?: string | null, public roll?: string, public income?: number) {}
}

export function getMoneyIdentifier(money: IMoney): number | undefined {
  return money.id;
}
