const types: { [key: string]: string[] } = {
    'application/x-executable': ['executable'],
    'application/x-www-form-urlencoded': ['urlencoded'],
    'image/x-mng': ['mng'],
    'text/x-yaml': ['yaml', 'yml'],
    'application/graphql': ['graphql'],
    'application/sql': ['sql'],
    'application/vnd.api+json': ['api'],
    'application/zstd': ['zst'],
    'image/flif': ['flif'],
    'multipart/form-data': ['data'],
    'text/php': ['php']
};
Object.freeze(types);
export default types;
