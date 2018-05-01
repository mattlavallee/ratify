import { Application, Request, Response } from "express";

function register(app: Application): Application {
  app.post('/group/join', (req: Request, res: Response) => {
    //TODO: implement me!
    const joinCode: string = req.body.code;

    res.status(200);
    res.json({
      error: false,
      code: joinCode,
    });
  });

  app.put('/group', (req: Request, res: Response) => {
    const foo = [];

    foo.push(1);
    foo.push('2');
    //TODO: implement me!
    res.status(200);
    res.json({
      error: false,
      id: '1234567890'
    });
  });

  app.get('/group', (req: Request, res: Response) => {
    //TODO: implement me!
    res.status(200);
    res.json({
      error: false,
      group: {
        id: '1234567890',
      },
    });
  });

  return app;
}

export default register;