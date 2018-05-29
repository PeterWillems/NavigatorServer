import {SeObjectModel} from './se-object.model';

export class PerformanceModel extends SeObjectModel {
  value: string;

  getQueryTag(): string {
    return '/performances';
  }
}
