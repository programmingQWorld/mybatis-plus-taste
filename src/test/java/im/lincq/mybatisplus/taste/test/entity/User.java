�}[�
   �<y�Z��[��qw
��7�:�F S���JE�߂V���n��W�Ao��p�Ė�o�lvߋFd~��9���zȾº�y�����e�j��vx:�ȳ#�z|?��CݥL�6j��o�M�:���ٗ�SԢ%"�9Y�>p�"�j�/�W���xU��nh��P�>��,��W�
��f=��d�o ����ɐ��+§|���ٖ�SS��A~ �d��Z ��4��!�z�up��9����w$�	h��kF���}?������YE|�(�J]E&�WJ���#w��)�X#򶟼&@+$���L�i��*���c/ ��#Q�YQƊؕ�a+����;�A�."h�p	�����A�SR$3�n�!ʋi������=sh��y�h���Ԑ�!�ZR�>G�X���O͏!ኌ��E{��P(�ʰ?@�,C� G Q_(�i��f�ay����&&[����vKCƱ-pk:� �r+�!��::�)9�a�7@������9�>l�&^��?�-������K�\�d��g/r�4��L��'`G�O|���o	oG�d�K����0�=)Ј���o^V����CT�غ�_�~uժ��WݸR{9�R���u�tj�U��}�=M��\>Ƿ��t�hvQn�90��mJ��[��"�5'6| ��^��lJn����D��n٬4.��sڈwF�Kc}�-��G��|���5��w;^BQ�7�q*)x�6
 X��Y�:T�����c�?�v�=yk�Vx����h�OEU\��2�p��	ݾ���H�AL�\Ь�-�ve�V�9?Q.n$Z䤚XT"_�3��#�y��4F[Mw>Yr2��n�#����#Ԩ6�)��Z��3FڰmK��Z8,R�PEY|	BRa	���[P�Η�2�RW��m��l7�����H�e��e�ԭ5�P�����L��yq�I�N�>��V�`2nNq＃�ĽYN�<1��� ]'/�]4]\q��JЕI��c�?��q��f%�\I�Zc�Ka�l�Qڳnj��[sH{��y��.�C�j�B*�����Ue���.=�1�ٚM�Ί�}�+�@r3s���G�Y�7rzbA�����U�U+Z笸��T��8cC����l��/��(2F��w�u�إm�'�D�P���pN؆�΂�[������<���v&�)���^�0�>h��	�/���3 -,��g'zf|i\����(�V�RXm@�5 H���ZK����<�/�1H�����?����c�.[�f�+���������a�r�3Z1b�j�Q"#��}#���!W�궐�D����2�U��vk�u$���67f�M���`�A%��^!]2��a`�:����^�|�LL\0	ɴ��7�K�J�E\��vL�2�)ͩ��T��B��^���\&?Sj��EL]�<F��"��q�⴫Q>+�ꋭ������Ƚ��$-��ȱz�!E��W�_�'�f�F {Ώ�D��x�Hfq_��	�]�^��"����#,���-�O�-ntln(User.class.getSimpleName());
        Field[] fields = user.getClass().getDeclaredFields();
        for (Field field : fields) {
            System.out.println("===================================");
            System.out.println(field.getName());
            System.out.println(field.getType().toString());
            field.setAccessible(true);
            System.out.println(field.get(user)); // 获取变量的值
        }
    }
}
