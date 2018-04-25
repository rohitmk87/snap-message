#/bin/bash

if [ $# -lt 1 ]; then
  REPORT_DIR="/tmp/licreport"
else
  REPORT_DIR=$1
fi

if [ ! -d ${REPORT_DIR} ]; then
 mkdir -p $REPORT_DIR
fi

find ${REPORT_DIR}  -name index.html  | awk -v suffix=${REPORT_DIR} -v replwith='.' 'BEGIN { printf("<html><head/><body><table>\n"); } {sub(suffix,replwith); printf("<tr><td><a href=\"%s\">%s</a></td></tr>\n", $0, $0);} END {printf("</table></body></html>\n"); }' > ${REPORT_DIR}/report.html
