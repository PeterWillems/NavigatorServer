import {SeObjectModel} from './se-object.model';

export class RealisationPortModel extends SeObjectModel {
  owner: string;
  performances: string[];

  getQueryTag(): string {
    return '/realisation-ports';
  }
}
