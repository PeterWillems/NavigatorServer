import {SeObjectModel} from './se-object.model';

export class RequirementModel extends SeObjectModel {
  minValue: string;
  maxValue: string;

  getQueryTag(): string {
    return '/requirements';
  }
}
