let lastId = 0;

export default function (prefix = 'id') {
  lastId += 1;
  return `${prefix}${lastId}`;
}
