import {OwnerInfo} from 'micro_service_modules/projekt/projekt-api';

export interface UserInfo extends OwnerInfo {
    uuid: string;
}
