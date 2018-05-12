import {SeObjectModel} from './se-object.model';

export class PerformanceModel extends SeObjectModel {
  getQueryTag(): string {
    return '/performances';
  }
}
