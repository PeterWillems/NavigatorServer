import {SeObjectModel} from './se-object.model';

export class RealisationModuleModel extends SeObjectModel {
  performances: string[];
  ports: string[];

  getQueryTag(): string {
    return '/realisation-modules';
  }
}
