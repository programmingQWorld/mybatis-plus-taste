�}��
 6  ���m!�6��qw
��H�:�F"S���JE�ߊ�<���B-��L
8���R�GgIJ�P.C�T��ސ��o���j4�^"~8fS�d�
����Ӯ	C'��[PhP�o�#�Q�vay�4��c�Ɩ��5���T�����2 S�� ���|m⮙�2u:񻢺I+����,~A���p� � 3�Ys����	�IM�����S���A\���Ϊ�|g����ʗ~��e�w���]$:��-���@�Q���2ƪ)��� ��e+��ci�U�5D��;c$�$�������7�Qg�"c�K��:���6Ĩ�4�7C?9�GC:�u�A��`̛�6�L��7�p	�����A�SR$3�n�!ʋi������=sh��y�h���Ԑ�!�ZR�>G�X���O͏!ኌ��E{��P(�ʰ?@�,C� G Q_(�i��f�ay����&&[����vKCƱ-pk:� �r+�!��::�)9�a�7@������9�>�找按照起始位置Number和结束位置Number获取区间内的行数据
         *  需要往外面补充两层循环.
         * */
        offset++;
        sql.insert(0, "SELECT U.*, ROWNUM R FROM(").append(") where rownum < ").append(offset + limit);
        sql.insert(0, "SELECT * FROM (").append(") TEMP WHERE R >= ").append(offset);
        return sql.toString();
    }
}
