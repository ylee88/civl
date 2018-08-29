      subroutine f_harmonic(alpha,beta,gamma,beta2,beta3,f)

      double precision alpha, beta, gamma, beta2, beta3, f

      double precision t1,t2,t3,t4,t5,t6,t7,t8,t9
      double precision t10,t11,t12,t13,t14,t15,t16,t17,t18,t19
      double precision t20,t21,t22,t23,t24,t25,t26,t27,t28,t29
      double precision t30,t31,t32,t33,t34,t35,t36,t37,t38,t39
      double precision t40,t41,t42,t43,t44,t45,t46,t47,t48,t49
      double precision t50,t51,t52,t53,t54,t55
      double precision t11_2,t31_2,t41_2,t30_2,t37_2,t44_2,t10_2,
     c                 t49_2,t28_2,t44_3,t46_2,t53_2,t55_2
      double precision f_0,f_1,f_2,f_3,f_4,f_5,f_6,f_7,f_8

      t1 = cos(beta)
      t2 = cos(alpha)
      t3 = sin(beta2)
      t4 = t2 - t3
      t5 = t2 + t3
      t6 = t1*t1
      t7 = t6 + 0.1e1*beta3
      t8 = cos(gamma)
      t9 = sin(gamma)
      t10 = t2*t2
      t11 = 0.2e1 * t2
      t12 = t10 + (-t11 - t3) * t3
      t11_2 = t10 + (t11 - t3) * t3
      t13 = 0.1e1 + (0.6e1 + t6) * t6
      t14 = t9*t9
      t15 = t8*t8
      t16 = t15*t15 + t14*t14
      t17 = t15 - t14
      t18 = sqrt(0.5e1)
      t19 = sin(beta)
      t20 = sqrt(0.3e1)
      t21 = sqrt(0.7e1)
      t22 = sqrt(0.14e2)
      t23 = sqrt(0.30e2)
      t24 = t8 - t9
      t25 = t8 + t9
      t26 = t19*t19
      t27 = t26*t26
      t28 = sqrt(0.2e1)
      t29 = sqrt(0.6e1)
      t30 = sqrt(0.42e2)
      t31 = 0.2e1 * t23
      t32 = t18 * (0.4e1 * t20 + t28 * t29) * t30 + t31 * t20 * t21
      t33 = t15 - t14 / 0.3e1
      t34 = t15 - 0.3e1 * t14
      t35 = t6 + 0.3e1
      t36 = t12 * t11_2
      t37 = t29 * t20
      t38 = t28 + t37
      t39 = -0.3e1 / 0.16e2
      t40 = t15 + t14
      t31_2 = t20 * (t6 * t18 * t29 + t31 * (t6 - t26 * t40 / 0.2e1)) 
     c     * t28 + (0.12e2 * t18 + t29 * t23) * t6
      t41 = t40 * t6
      t42 = t41 - t15 - t14 - 0.2e1 * t26
      t43 = sqrt(0.10e2)
      t44 = sqrt(0.15e2)
      t45 = t20 * t28
      t46 = t45 * t42
      t41_2 = t46 * t43 + (t41 - t15 - t14 - 0.8e1 * t26) * t44
      t47 = t1 - 0.1e1
      t48 = t1 + 0.1e1
      t30_2 = alpha*t41_2 * sqrt(0.105e3) - 0.30e2 * t30 * t26 * t29 
     c     + 0.15e2 * t40 * t47 * t48 * t21
      t49 = beta*t3*t3
      t50 = t10*t10 + (-0.6e1 * t10 + t49) * t49*beta2
      t51 = 0.4e1 * t2 * t3
      t37_2 = (t45 * (t6 - 0.3e1 / 0.8e1 * t26 * t40) + t29 * t6 / 
     c     0.2e1) * t44 + (-t37 * t26 * t40 / 0.2e1 + 0.3e1 / 0.2e1 
     c     * t28 * (t6 - 0.5e1 / 0.4e1 * t26 * t40)) * t18
      t44_2 = sqrt(0.70e2)
      t52 = sqrt(0.21e2)
      t10_2 = t51 * (t10 - t49)*beta3
      t49_2 = t50 * t1
      t53 = t18 / 0.1680e4*beta2
      t28_2 = t53 * ((0.6e1 * t28 * t44_2 * t43 + 0.15e2 * t52 * t29) * 
     c     t48 * t47 * t40 + t46 * t44_2 * t23 + t41_2 * sqrt(0.210e3))
      t44_3 = t1 * t21 / 0.6e1*beta*gamma
      t46_2 = -t26 * t40 / 0.4e1
      t53_2 = t26 * t25 * t24 * t31_2 * t21 / 0.24e2 - t53 * t30_2 * 
     c     (t36 * t7 * t25 * t24 - 0.16e2 * t2*t8*t3*t9*t4*t5*t1)
      t54 = t19 * (t1 * t32 * t8 * t26 * t34 * t21 / 0.96e2 - t38 * 
     c     ((-0.36e2 * t6 - 0.12e2) * t33 * t9 * t5 * t4 * t3 * t2 + 
     c     t36 * t8 * t34 * t1 * t35) * t18 / 0.16e2)
      t55 = 0.2e1 * t8
      t55_2 = (-0.384e3 * t8 * t9 * t17 * t7 * t5 * t4 * t3 * t2 * t1 
     c     + t36 * t13 * (0.6e1 * t16 - 0.36e2 * t14 * t15)) * t18 / 
     c     0.48e2 + t27 * t20 * t21 * t22 * t23 * (t15 + (-t55 - t9) * 
     c     t9) * (t15 + (t55 - t9) * t9) / 0.48e2
      
      f_0 = ((0.24e2 * t16 - 0.144e3 * t14 * t15) * t7 * t5 * t4 * t3 
     c     * t2 * t1 + 0.6e1 * t9 * t12 * t11_2 * t13 * t8 * t17) * t18 
     c     / 0.12e2 + t27*t8*t9*t20*t21*t22*t23*t24*t25 / 0.12e2
      f_1 = t19 * (t9 * t1 * t26 * t32 * t33 * t21 / 0.32e2 + t39 * 
     c     ((0.4e1 * t6 + 0.4e1 / 0.3e1) * t34 * t8 * t5 * t4 * t3 * 
     c     t2 + t36 * t9 * t33 * t1 * t35) * t38 * t18)
      f_2 = t21 * t9 * t31_2 * t26 * t8 / 0.12e2 - t30_2 * (t7 * t50 * 
     c     t9 * t8 + t51 * t24 * t25 * t4 * t5 * t1) * t18 / 0.840e3
      f_3 = t19 * t44_3 * t9 * t37_2 + t28_2 * (t49_2 * t9 + t10_2 * t8)
      f_4 = -t36 * (-t40 * t47 * t41_2 * t48 * t52 / 0.2e1 + t26 * (t23 
     c     * t42 * t20 + 0.6e1 * t43 * t40 * t47 * t48) * t22) * t18 / 
     c     0.420e3 + t21 * (t46_2 * t45 * t29 * (t6 + t46_2) + (t6 - 
     c     0.3e1 / 0.2e1 * t26 * t40) * t6)
      f_5 = t19 * (t44_3*t37_2*t8 + t28_2*(t49_2 * t8 - t10_2 * t9))
      f_6 = t53_2
      f_7 = t54*1
      f_8 = t55_2
  
      f = f_0 + f_1 + f_2 + f_3 + f_4 + f_5 + f_6 + f_7 + f_8

      return
      end