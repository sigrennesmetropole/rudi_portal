import {OwnerInfo} from '../../projekt/projekt-api';

export interface UserInfo extends OwnerInfo {
    uuid: string;
}
