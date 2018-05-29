export abstract class SeObjectModel {
  uri: string;
  localName: String;
  label: string;
  assembly: string;
  parts: string[];

  abstract getQueryTag(): string;
}
