#!/bin/bash

if [ -n "${HTTP_PROXY_HOST}" ]; then
    export JAVA_OPTS="$JAVA_OPTS -Dhttp.proxyHost=${HTTP_PROXY_HOST} -Dhttp.proxyPort=${HTTP_PROXY_PORT} -Dhttp.nonProxyHosts=${HTTP_NON_PROXY_HOSTS}"
fi

if [ -n "${HTTPS_PROXY_HOST}" ]; then
    export JAVA_OPTS="$JAVA_OPTS -Dhttps.proxyHost=${HTTPS_PROXY_HOST} -Dhttps.proxyPort=${HTTPS_PROXY_PORT} -Dhttps.nonProxyHosts=${HTTPS_NON_PROXY_HOSTS}"
fi


echo "                             ,                                                                             "
echo "                                ******,                                                                    "
echo "                                       ***.                                                                "
echo "                                    ....(*****.                                                            "
echo "                                           /*******,                                                       "
echo "                                          //***********                                                    "
echo "                                         //((((                                                            "
echo "                                       /((((((                                                             "
echo "                                     (((#%%%%,                                                             "
echo "                                  (#%%%%%%%%%,                                                             "
echo "                               #%%%%%%%%%%%%%,                                                             "
echo "                           %%%%%%%%%%%%%%%%%%                                                              "
echo "                      ,%%%%%%%%%%%%%%%%%%%%%*                                                              "
echo "           /((((((#%%%%%%%##(((((((((##%%%%                                                                "
echo "              %%(((((/,.....        ..(((                                                                  "
echo "            %%%%%*....             .,((                                                                    "
echo "          /%%%/...                .((                                                                      "
echo "         %%%,.                  ./.                                                                        "
echo "       #%%,.        ,#(,       ,                                                %%%   #%%                  "
echo "      %%(      .%%%%/  (%%%%                                                    %%%   %%%                  "
echo "     %%       %%%          ,       %%%%%%%% %%%   %%%%%%%%%%%#    %%%%%%%%#     %%%   %%%      %%%%%%%%    "
echo "    %.       .%%                 %%%,      %%%%          %%%(   %%%       %%#   %%%   %%%   .%%*      ,%%  "
echo "  *%         /%%      %%%%%%%%   %%*        %%%        %%%,    /%%%%%%%%%%%%%.  %%%   %%%   %%%%%%%%%%%%%% "
echo " (            %%%          %%%   %%(        %%%      %%%       *%%              %%%   %%%   %%#            "
echo ",              %%%(      #%%%    /%%#      %%%%    %%%          %%%.      %%%   %%%   %%%    %%%      ,%%  "
echo "               ,%%%%%%%%         *%%%%%%* %%%   %%%%%%%%%%%.    ,%%%%%%%#     %%%   #%%      %%%%%%%%.     "



# Run the script
/usr/local/s2i/run